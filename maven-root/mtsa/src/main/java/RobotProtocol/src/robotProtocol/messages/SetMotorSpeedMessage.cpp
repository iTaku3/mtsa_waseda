/*
 * SetMotorSpeedMessage.cpp
 *
 *  Created on: Apr 5, 2013
 *      Author: mariano
 */

#include "SetMotorSpeedMessage.h"

namespace RobotProtocol {

SetMotorSpeedMessage SetMotorSpeedMessage::staticMessage = SetMotorSpeedMessage(0,0,0,0);

Message *SetMotorSpeedMessage::deserialize(char *serializedMessage){
	SetMotorSpeedMessage::staticMessage.initialize(serializedMessage);
	return &(SetMotorSpeedMessage::staticMessage);
}

SetMotorSpeedMessage::SetMotorSpeedMessage(int to, int from, int leftSpeed, int rightSpeed)
:Message(MESSAGE_ID, 1, to, from) {
	this->leftSpeed		= leftSpeed;
	this->rightSpeed	= rightSpeed;
}

SetMotorSpeedMessage::SetMotorSpeedMessage(char *serializedMessage){
	initialize(serializedMessage);
}

SetMotorSpeedMessage::~SetMotorSpeedMessage() {
}

int SetMotorSpeedMessage::getMessageLength(){
	return Message::getMessageLength() + LEFT_FIELD_LENGTH + RIGHT_FIELD_LENGTH;
}

int SetMotorSpeedMessage::getLeftSpeed(){
	return leftSpeed;
}

int SetMotorSpeedMessage::getRightSpeed(){
	return rightSpeed;
}

void SetMotorSpeedMessage::readData(char **serializedMessageIndex){
	char *serializedMessage	= *serializedMessageIndex;
	char leftField[LEFT_FIELD_LENGTH + 1];
	char rightField[RIGHT_FIELD_LENGTH + 1];
	int index = 0;
	//int messageLength = (int)strtol(*serializedMessageIndex, &next, LENGTH_FIELD_LENGTH);
	leftField[LEFT_FIELD_LENGTH] = rightField[RIGHT_FIELD_LENGTH] = '\0';
	memcpy(leftField, &(serializedMessage[index]), LEFT_FIELD_LENGTH);
	leftSpeed = (int)strtol(leftField, NULL, 10);
	index += LEFT_FIELD_LENGTH;
	memcpy(rightField, &(serializedMessage[index]), RIGHT_FIELD_LENGTH);
	rightSpeed = (int)strtol(rightField, NULL, 10);
	index += RIGHT_FIELD_LENGTH;
	(*serializedMessageIndex) += index;
}

void SetMotorSpeedMessage::writeData(char **serializedMessageIndex){
	sprintf(*serializedMessageIndex,"%04d%04d", leftSpeed, rightSpeed);
	(*serializedMessageIndex) = (*serializedMessageIndex) + LEFT_FIELD_LENGTH + RIGHT_FIELD_LENGTH;
}

} /* namespace RobotProtocol */
