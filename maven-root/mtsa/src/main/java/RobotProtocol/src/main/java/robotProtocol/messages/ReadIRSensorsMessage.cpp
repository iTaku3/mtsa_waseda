/*
 * ReadIRSensorsMessage.cpp
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */

#include "ReadIRSensorsMessage.h"


namespace RobotProtocol {

ReadIRSensorsMessage ReadIRSensorsMessage::staticMessage = ReadIRSensorsMessage(0,0);

Message *ReadIRSensorsMessage::deserialize(char *serializedMessage){
	ReadIRSensorsMessage::staticMessage.initialize(serializedMessage);
	return &(ReadIRSensorsMessage::staticMessage);
}

ReadIRSensorsMessage::ReadIRSensorsMessage(int to, int from)
:Message(MESSAGE_ID, 1, to, from){}

ReadIRSensorsMessage::ReadIRSensorsMessage(char *serializedMessage){
	initialize(serializedMessage);
}

ReadIRSensorsMessage::~ReadIRSensorsMessage() {
}

int ReadIRSensorsMessage::getMessageLength(){
	return Message::getMessageLength();
}

void ReadIRSensorsMessage::readData(char **serializedMessageIndex){}

void ReadIRSensorsMessage::writeData(char **serializedMessageIndex){}


} /* namespace RobotProtocol */

