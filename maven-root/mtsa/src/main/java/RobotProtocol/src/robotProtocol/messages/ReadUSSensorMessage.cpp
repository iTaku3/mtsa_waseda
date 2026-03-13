/*
 * ReadIRSensorsMessage.cpp
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */

#include "ReadUSSensorMessage.h"


namespace RobotProtocol {

ReadUSSensorMessage ReadUSSensorMessage::staticMessage = ReadUSSensorMessage(0,0);

Message *ReadUSSensorMessage::deserialize(char *serializedMessage){
	ReadUSSensorMessage::staticMessage.initialize(serializedMessage);
	return &(ReadUSSensorMessage::staticMessage);
}

ReadUSSensorMessage::ReadUSSensorMessage(int to, int from)
:Message(MESSAGE_ID, 1, to, from){}

ReadUSSensorMessage::ReadUSSensorMessage(char *serializedMessage){
	initialize(serializedMessage);
}

ReadUSSensorMessage::~ReadUSSensorMessage() {
}

int ReadUSSensorMessage::getMessageLength(){
	return Message::getMessageLength();
}

void ReadUSSensorMessage::readData(char **serializedMessageIndex){}

void ReadUSSensorMessage::writeData(char **serializedMessageIndex){}


} /* namespace RobotProtocol */

