/*
 * ReadIRSensorsReplyMessage.cpp
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */

#include "ReadUSSensorReplyMessage.h"

namespace RobotProtocol {

ReadUSSensorReplyMessage ReadUSSensorReplyMessage::staticMessage = ReadUSSensorReplyMessage(0,0,0);

Message *ReadUSSensorReplyMessage::deserialize(char *serializedMessage){
	ReadUSSensorReplyMessage::staticMessage.initialize(serializedMessage);
	return &(ReadUSSensorReplyMessage::staticMessage);
}

ReadUSSensorReplyMessage::ReadUSSensorReplyMessage(int to, int from, int distance)
:Message(MESSAGE_ID, 1, to, from) {
	this->distance		= distance;
}

ReadUSSensorReplyMessage::ReadUSSensorReplyMessage(char *serializedMessage){
	initialize(serializedMessage);
}

ReadUSSensorReplyMessage::~ReadUSSensorReplyMessage() {
}

int ReadUSSensorReplyMessage::getMessageLength(){
	return Message::getMessageLength() + DISTANCE_FIELD_LENGTH;
}

int ReadUSSensorReplyMessage::getDistance(){
	return distance;
}

void ReadUSSensorReplyMessage::setDistance(int distance){
	this->distance		= distance;
}


void ReadUSSensorReplyMessage::readData(char **serializedMessageIndex){
	char *serializedMessage	= *serializedMessageIndex;
	char distanceField[DISTANCE_FIELD_LENGTH + 1];
	int index = 0;
	//int messageLength = (int)strtol(*serializedMessageIndex, &next, LENGTH_FIELD_LENGTH);
	distanceField[DISTANCE_FIELD_LENGTH] = '\0';
	memcpy(distanceField, &(serializedMessage[index]), DISTANCE_FIELD_LENGTH);
	distance = (int)strtol(distanceField, NULL, 10);
	index += DISTANCE_FIELD_LENGTH;
	(*serializedMessageIndex) += index;
}

void ReadUSSensorReplyMessage::writeData(char **serializedMessageIndex){
	sprintf(*serializedMessageIndex,"%04d", distance);
	(*serializedMessageIndex) = (*serializedMessageIndex) + DISTANCE_FIELD_LENGTH;
}

} /* namespace RobotProtocol */

