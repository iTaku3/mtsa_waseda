/*
 * ReadIRSensorsReplyMessage.cpp
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */

#include "RobotStatusMessage.h"

namespace RobotProtocol {

RobotStatusMessage RobotStatusMessage::staticMessage = RobotStatusMessage(0,0,0,0,0,0,0);

Message *RobotStatusMessage::deserialize(char *serializedMessage){
	RobotStatusMessage::staticMessage.initialize(serializedMessage);
	return &(RobotStatusMessage::staticMessage);
}

RobotStatusMessage::RobotStatusMessage(int to, int from, int leftSpeedValue, int rightSpeedValue,
		int leftIRValue, int rightIRValue, int batteryValue)
:Message(MESSAGE_ID, 1, to, from) {
	this->leftSpeedValue		= leftSpeedValue;
	this->rightSpeedValue		= rightSpeedValue;
	this->leftIRValue			= leftIRValue;
	this->rightIRValue			= rightIRValue;
	this->batteryValue			= batteryValue;
}

RobotStatusMessage::RobotStatusMessage(char *serializedMessage){
	initialize(serializedMessage);
}

RobotStatusMessage::~RobotStatusMessage() {
}

int RobotStatusMessage::getMessageLength(){
	return Message::getMessageLength() + LEFT_SPEED_FIELD_LENGTH + RIGHT_SPEED_FIELD_LENGTH
			+ LEFT_IR_FIELD_LENGTH + RIGHT_IR_FIELD_LENGTH + BATTERY_FIELD_LENGTH;
}

int RobotStatusMessage::getLeftSpeedValue(){
	return leftSpeedValue;
}

int RobotStatusMessage::getRightSpeedValue(){
	return rightSpeedValue;
}

int RobotStatusMessage::getLeftIRValue(){
	return leftIRValue;
}

int RobotStatusMessage::getRightIRValue(){
	return rightIRValue;
}

int RobotStatusMessage::getBatteryValue(){
	return batteryValue;
}

void RobotStatusMessage::setLeftSpeedValue(int leftSpeedValue){
	this->leftSpeedValue	= leftSpeedValue;
}

void RobotStatusMessage::setRightSpeedValue(int rightSpeedValue){
	this->rightSpeedValue	= rightSpeedValue;
}

void RobotStatusMessage::setLeftIRValue(int leftIRValue){
	this->leftIRValue		= leftIRValue;
}

void RobotStatusMessage::setRightIRValue(int rightIRValue){
	this->rightIRValue		= rightIRValue;
}

void RobotStatusMessage::setBatteryValue(int batteryValue){
	this->batteryValue		= batteryValue;
}

void RobotStatusMessage::readData(char **serializedMessageIndex){
	char *serializedMessage	= *serializedMessageIndex;
	char leftSpeedField[LEFT_SPEED_FIELD_LENGTH + 1];
	char rightSpeedField[RIGHT_SPEED_FIELD_LENGTH + 1];
	char leftIRField[LEFT_IR_FIELD_LENGTH + 1];
	char rightIRField[RIGHT_IR_FIELD_LENGTH + 1];
	char batteryField[BATTERY_FIELD_LENGTH + 1];
	int index = 0;
	leftSpeedField[LEFT_SPEED_FIELD_LENGTH] = rightSpeedField[RIGHT_SPEED_FIELD_LENGTH] = '\0';
	leftIRField[LEFT_IR_FIELD_LENGTH] = rightIRField[RIGHT_IR_FIELD_LENGTH] = '\0';
	batteryField[BATTERY_FIELD_LENGTH] = '\0';
	memcpy(leftSpeedField, &(serializedMessage[index]), LEFT_SPEED_FIELD_LENGTH);
	leftSpeedValue = (int)strtol(leftSpeedField, NULL, 10);
	index += LEFT_SPEED_FIELD_LENGTH;
	memcpy(rightSpeedField, &(serializedMessage[index]), RIGHT_SPEED_FIELD_LENGTH);
	rightSpeedValue = (int)strtol(rightSpeedField, NULL, 10);
	index += RIGHT_SPEED_FIELD_LENGTH;
	memcpy(leftIRField, &(serializedMessage[index]), LEFT_IR_FIELD_LENGTH);
	leftIRValue = (int)strtol(leftIRField, NULL, 10);
	index += LEFT_IR_FIELD_LENGTH;
	memcpy(rightIRField, &(serializedMessage[index]), RIGHT_IR_FIELD_LENGTH);
	rightIRValue = (int)strtol(rightIRField, NULL, 10);
	index += RIGHT_IR_FIELD_LENGTH;
	memcpy(batteryField, &(serializedMessage[index]), BATTERY_FIELD_LENGTH);
	batteryValue = (int)strtol(batteryField, NULL, 10)	;
	index += BATTERY_FIELD_LENGTH;
	(*serializedMessageIndex) += index;
}

void RobotStatusMessage::writeData(char **serializedMessageIndex){
	sprintf(*serializedMessageIndex,"%04d%04d%04d%04d%04d", leftSpeedValue, rightSpeedValue, leftIRValue, rightIRValue, batteryValue);
	(*serializedMessageIndex) = (*serializedMessageIndex) + LEFT_SPEED_FIELD_LENGTH + RIGHT_SPEED_FIELD_LENGTH
			+ LEFT_IR_FIELD_LENGTH + RIGHT_IR_FIELD_LENGTH + BATTERY_FIELD_LENGTH;
}

} /* namespace RobotProtocol */

