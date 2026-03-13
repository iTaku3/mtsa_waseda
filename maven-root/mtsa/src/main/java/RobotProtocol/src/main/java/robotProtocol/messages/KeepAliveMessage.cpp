/*
 * KeepAliveMessage.cpp
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#include "KeepAliveMessage.h"

namespace RobotProtocol {

KeepAliveMessage KeepAliveMessage::staticMessage = KeepAliveMessage(0,0,0);

Message *KeepAliveMessage::deserialize(char *serializedMessage){
	KeepAliveMessage::staticMessage.initialize(serializedMessage);
	return &(KeepAliveMessage::staticMessage);
}

KeepAliveMessage::KeepAliveMessage():Message(MESSAGE_ID, 1, -1, -1){}

KeepAliveMessage::KeepAliveMessage(int to, int from, int batteryLeft)
:Message(MESSAGE_ID, 1, to, from) {
	this->batteryLeft	= batteryLeft;
}

KeepAliveMessage::KeepAliveMessage(char *serializedMessage){
	initialize(serializedMessage);
}

KeepAliveMessage::~KeepAliveMessage() {
}

int KeepAliveMessage::getMessageLength(){
	return Message::getMessageLength() + BATTERY_FIELD_LENGTH;
}

int KeepAliveMessage::getBatteryLeft(){
	return batteryLeft;
}

void KeepAliveMessage::setBatteryLeft(int batteryLeft){
	this->batteryLeft	= batteryLeft;
}


void KeepAliveMessage::readData(char **serializedMessageIndex){
	char *serializedMessage	= *serializedMessageIndex;
	char batteryLeftField[BATTERY_FIELD_LENGTH + 1];
	int index = 0;
	//int messageLength = (int)strtol(*serializedMessageIndex, &next, LENGTH_FIELD_LENGTH);
	batteryLeftField[BATTERY_FIELD_LENGTH] = '\0';
	memcpy(batteryLeftField, &(serializedMessage[index]), BATTERY_FIELD_LENGTH);
	batteryLeft = (int)strtol(batteryLeftField, NULL, 10);
	index += BATTERY_FIELD_LENGTH;
	(*serializedMessageIndex) += index;
}

void KeepAliveMessage::writeData(char **serializedMessageIndex){
	sprintf(*serializedMessageIndex,"%04d", batteryLeft);
	(*serializedMessageIndex) = (*serializedMessageIndex) + BATTERY_FIELD_LENGTH;
}

} /* namespace RobotProtocol */
