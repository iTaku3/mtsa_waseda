/*
 * N6.cpp
 *
 *  Created on: Apr 5, 2013
 *      Author: mariano
 */

#include "N6.h"

namespace RobotProtocol {

N6::N6(MessageManager *manager, int robotId) {
	initialize(manager, robotId, MASTER_UNDEFINED);
}

N6::N6(MessageManager *manager, int robotId, int masterId) {
	initialize(manager, robotId, masterId);
}

void N6::initialize(MessageManager *manager, int robotId, int masterId) {

	this->manager		= manager;
	this->robotId		= robotId;
	this->masterId		= masterId;

	lastCommandIndex	= -1;
	cycles				= 0;

	leftIRSensor		= 0;
	rightIRSensor		= 0;

	for(int i = 0; i < MAX_COMMAND_AMOUNT; i++){
		commands[i]	= NULL;
		commandsMids[i]	= -1;
	}

	init();

	Serial.begin(9600);
	motor1.setClockwise(false);
	motor0.setClockwise(true);
#if defined(UBRR1H)
	#warning Serial1 defined at 9600
	Serial1.begin(9600);
	beepConfirm();
	delay(60);
#endif
}

N6::~N6() {
	// TODO Auto-generated destructor stub
}

int N6::getRobotId(){
	return robotId;
}

int N6::getMasterId(){
	return masterId;
}

void N6::beepError(){
	tone(BUZZER_PIN, 10*BASE_FREQ, 5);
	delay(30);
}

void N6::beepConfirm(){
	tone(BUZZER_PIN, 2*BASE_FREQ, 5);
	delay(10);
}

void N6::beepMasterSet(){
	tone(BUZZER_PIN, 20*BASE_FREQ, 80);
	delay(80);
	tone(BUZZER_PIN, 20*BASE_FREQ, 40);
	delay(30);
}

void N6::beepMasterClear(){
	tone(BUZZER_PIN, 40*BASE_FREQ, 40);
	delay(80);
	tone(BUZZER_PIN, 40*BASE_FREQ, 80);
	delay(30);
}

int N6::getMidIndex(int mid){
	if(lastCommandIndex < 0)
		return -1;

	for(int i = 0; i <= lastCommandIndex; i++){
		if(commandsMids[i] == mid)
			return i;
	}
	return -1;
}

bool N6::addCommand(int mid, Command *command){
	if(mid < 0){
		printf("Mid %i was out of range\n", mid);
		return false;
	}

	int midIndex	= getMidIndex(mid);

	if(midIndex != -1){
		printf("Command for Mid %i was already added\n", mid);
		return false;
	}

	lastCommandIndex++;

	commands[lastCommandIndex]		= command;
	commandsMids[lastCommandIndex]	= mid;

	return true;
}

void N6::processCommandMessage(Message *message, int newCommandIndex){
	Command *messageCommand	= commands[newCommandIndex];

	if(messageCommand->isContinuous()){
		if(currentCommand != NULL){
			currentCommand->reset();
		}

		CommandAcceptedMessage commandAcceptedMessage	= CommandAcceptedMessage(getMasterId(), getRobotId(), message->getMessageID());
		manager->sendMessage(commandAcceptedMessage);

		currentCommand	= messageCommand;
		currentCommand->resetFromMessage(message);
	}else{
		messageCommand->resetFromMessage(message);
		messageCommand->execute(0);
	}

}

bool N6::setCommand(int mid){
	if(mid < 0){
		printf("Mid %i was out of range\n", mid);
		return false;
	}

	int midIndex	= getMidIndex(mid);

	if(midIndex == -1){
		printf("No Command for Mid %i was added\n", mid);
		return false;
	}

	currentCommand	= commands[midIndex];
	currentCommand->reset();

	return true;
}

void N6::update(int updateGap){
	leftIRSensor		= analogRead(LEFT_LINE_SENSOR);
	rightIRSensor		= analogRead(RIGHT_LINE_SENSOR);
	topIRSensor			= analogRead(TOP_IR_SENSOR);

	cycles++;

	if(currentCommand != NULL){
		if(currentCommand->isDone()){
			currentCommand	= NULL;
		}else{
			currentCommand->execute(updateGap);
		}
	}

	Message* currentMessage;
	int newCommandIndex = -1;

	bool hasPendingMessage = manager->hasPendingMessage();

	return;

	if(hasPendingMessage){

		currentMessage	= manager->getNextMessage();

		if(currentMessage->getToAddress() == getRobotId()
				&& (getMasterId() == MASTER_UNDEFINED || currentMessage->getFromAddress() == getMasterId())){

			if(currentMessage->getMessageID() == CommunicationStartMessage::MESSAGE_ID){
				masterId		= currentMessage->getFromAddress();
				CommunicationStartAcknowledgeMessage communicationStartAckMessage	= CommunicationStartAcknowledgeMessage(getMasterId(), getRobotId());
				manager->sendMessage(communicationStartAckMessage);
				beepMasterSet();
			}else if(currentMessage->getMessageID() == CommunicationStopMessage::MESSAGE_ID){
				CommandAcceptedMessage commandAcceptedMessage	= CommandAcceptedMessage(getMasterId(), getRobotId(), currentMessage->getMessageID());
				manager->sendMessage(commandAcceptedMessage);
				masterId		= MASTER_UNDEFINED;
				beepMasterClear();
			}else{
				if(getMasterId() != MASTER_UNDEFINED){
					newCommandIndex	= getMidIndex(currentMessage->getMessageID());

					if(newCommandIndex >= 0){
						processCommandMessage(currentMessage, newCommandIndex);
					}else{
						CommandErrorMessage commandErrorMessage			= CommandErrorMessage(getMasterId(), getRobotId(), currentMessage->getMessageID(), Message::COMMAND_ID_UNKNOWN);
						manager->sendMessage(commandErrorMessage);
						beepError();
					}
				}else{
					CommandErrorMessage commandErrorMessage			= CommandErrorMessage(getMasterId(), getRobotId(), currentMessage->getMessageID(), Message::MASTER_UNDEFINED);
					manager->sendMessage(commandErrorMessage);
					beepError();
				}
			}
		}

		if(currentMessage != NULL)
			delete currentMessage;
		delay(50);

	}else{
		if(cycles % 6500 == 0){
			beepConfirm();
/*
			if(getMasterId() != MASTER_UNDEFINED && currentCommand == NULL){
				keepAliveMessage.setToAddress(getMasterId());
				keepAliveMessage.setFromAddress(getRobotId());
				keepAliveMessage.setBatteryLeft((int)topIRSensor);
				manager->sendMessage(keepAliveMessage);
			}
*/
		}
	}
	return;
}

void N6::setLeftMotorSpeed(float speed){
	motor1.setSpeed((float)speed);
}

void N6::setRightMotorSpeed(float speed){
	motor0.setSpeed((float)speed);
}

float N6::getLeftMotorSpeed(){
	return motor0.getSpeed();
}

float N6::getRightMotorSpeed(){
	return motor1.getSpeed();
}

int N6::getLeftIRSensor(){
	return leftIRSensor;
}

int N6::getRightIRSensor(){
	return rightIRSensor;
}

int N6::getTopIRSensor(){
	return topIRSensor;
}

int N6::getBatteryLeft(){
	return analogRead(BATTERY_PIN);
}

int N6::getUSSensorDistance(){
	return (int)getDistanceToObstacleInCentimeters();
}

long N6::getDistanceToObstacleInCentimeters(){

	  // The PING))) is triggered by a HIGH pulse of 2 or more microseconds.
	  // Give a short LOW pulse beforehand to ensure a clean HIGH pulse:
	  pinMode(RANGE_PIN, OUTPUT);
	  digitalWrite(RANGE_PIN, LOW);
	  delayMicroseconds(2);
	  digitalWrite(RANGE_PIN, HIGH);
	  delayMicroseconds(5);
	  digitalWrite(RANGE_PIN, LOW);

	  // The same pin is used to read the signal from the PING))): a HIGH
	  // pulse whose duration is the time (in microseconds) from the sending
	  // of the ping to the reception of its echo off of an object.
	  pinMode(RANGE_PIN, INPUT);

	  // convert the time into a distance
	  return pulseIn(RANGE_PIN, HIGH) / 29 / 2;
}

bool N6::isGoingLeft(){
	return (leftIRSensor > N6::LE_WHITE_L) && (rightIRSensor < N6::GE_GREY_R);
}

bool N6::isGoingRight(){
	return (rightIRSensor > N6::LE_WHITE_R) && (leftIRSensor < N6::GE_GREY_L);
}

bool N6::isOnWhite(){
	return (leftIRSensor > N6::LE_WHITE_L) && (rightIRSensor > N6::LE_WHITE_R);
}

bool N6::isOnBlack(){
	return (leftIRSensor < N6::GE_BLACK_L) && (rightIRSensor < N6::GE_BLACK_R);
}

bool N6::isAlmostOnBlack(){
	return ((leftIRSensor < N6::GE_BLACK_L) || (rightIRSensor < N6::GE_BLACK_R))
			&& (leftIRSensor < N6::GE_GREY_L && rightIRSensor < N6::GE_GREY_R);
}

bool N6::isLeftOnBlack(){
	return (leftIRSensor < N6::GE_BLACK_L);
}

bool N6::isRightOnBlack(){
	return (rightIRSensor < N6::GE_BLACK_R);
}


bool N6::isOnGray(){
	return ((leftIRSensor < N6::GE_GREY_L) && (leftIRSensor > N6::LE_GREY_L) &&
		      (rightIRSensor < N6::GE_GREY_R) && (rightIRSensor > N6::LE_GREY_R)) ||
		      ((leftIRSensor < N6::GE_GREY_L) && (leftIRSensor > N6::LE_GREY_L) &&
		      (rightIRSensor > N6::LE_WHITE_R)) ||
		      ((leftIRSensor > N6::LE_WHITE_L) &&
		      (rightIRSensor < N6::GE_GREY_R) && (rightIRSensor > N6::LE_GREY_R));
}

bool N6::isOnTrack(){
	return !isOnWhite();
}

MessageManager* N6::getMessageManager(){
	return manager;
}
}
/* namespace RobotProtocol */
