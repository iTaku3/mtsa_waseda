/*
 * ReadIRSensorsReplyMessage.cpp
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */

#include "ReadIRSensorsReplyMessage.h"

namespace RobotProtocol {

ReadIRSensorsReplyMessage ReadIRSensorsReplyMessage::staticMessage = ReadIRSensorsReplyMessage(0,0,0,0,0);

Message *ReadIRSensorsReplyMessage::deserialize(char *serializedMessage){
	ReadIRSensorsReplyMessage::staticMessage.initialize(serializedMessage);
	return &(ReadIRSensorsReplyMessage::staticMessage);
}

ReadIRSensorsReplyMessage::ReadIRSensorsReplyMessage(int to, int from, int leftValue, int rightValue, int topValue)
:Message(MESSAGE_ID, 1, to, from) {
	this->leftValue		= leftValue;
	this->rightValue	= rightValue;
	this->topValue		= topValue;
}

ReadIRSensorsReplyMessage::ReadIRSensorsReplyMessage(char *serializedMessage){
	initialize(serializedMessage);
}

ReadIRSensorsReplyMessage::~ReadIRSensorsReplyMessage() {
}

int ReadIRSensorsReplyMessage::getMessageLength(){
	return Message::getMessageLength() + LEFT_FIELD_LENGTH + RIGHT_FIELD_LENGTH + TOP_FIELD_LENGTH;
}

int ReadIRSensorsReplyMessage::getLeftValue(){
	return leftValue;
}

int ReadIRSensorsReplyMessage::getRightValue(){
	return rightValue;
}

int ReadIRSensorsReplyMessage::getTopValue(){
	return topValue;
}

void ReadIRSensorsReplyMessage::setLeftValue(int leftValue){
	this->leftValue		= leftValue;
}

void ReadIRSensorsReplyMessage::setRightValue(int rightValue){
	this->rightValue	= rightValue;
}

void ReadIRSensorsReplyMessage::setTopValue(int topValue){
	this->topValue		= topValue;
}

void ReadIRSensorsReplyMessage::readData(char **serializedMessageIndex){
	char *serializedMessage	= *serializedMessageIndex;
	char leftField[LEFT_FIELD_LENGTH + 1];
	char rightField[RIGHT_FIELD_LENGTH + 1];
	char topField[TOP_FIELD_LENGTH + 1];
	int index = 0;
	//int messageLength = (int)strtol(*serializedMessageIndex, &next, LENGTH_FIELD_LENGTH);
	leftField[LEFT_FIELD_LENGTH] = rightField[RIGHT_FIELD_LENGTH] = '\0';
	memcpy(leftField, &(serializedMessage[index]), LEFT_FIELD_LENGTH);
	leftValue = (int)strtol(leftField, NULL, 10);
	index += LEFT_FIELD_LENGTH;
	memcpy(rightField, &(serializedMessage[index]), RIGHT_FIELD_LENGTH);
	rightValue = (int)strtol(rightField, NULL, 10);
	index += RIGHT_FIELD_LENGTH;
	memcpy(topField, &(serializedMessage[index]), TOP_FIELD_LENGTH);
	topValue = (int)strtol(topField, NULL, 10);
	index += TOP_FIELD_LENGTH;
	(*serializedMessageIndex) += index;
}

void ReadIRSensorsReplyMessage::writeData(char **serializedMessageIndex){
	sprintf(*serializedMessageIndex,"%04d%04d%04d", leftValue, rightValue, topValue);
	(*serializedMessageIndex) = (*serializedMessageIndex) + LEFT_FIELD_LENGTH + RIGHT_FIELD_LENGTH + TOP_FIELD_LENGTH;
}

} /* namespace RobotProtocol */

