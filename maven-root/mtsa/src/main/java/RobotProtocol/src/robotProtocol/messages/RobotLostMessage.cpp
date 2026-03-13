/*
 * RobotLostMessage.cpp
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#include "RobotLostMessage.h"

namespace RobotProtocol {

RobotLostMessage RobotLostMessage::staticMessage = RobotLostMessage(0,0);

Message *RobotLostMessage::deserialize(char *serializedMessage){
	RobotLostMessage::staticMessage.initialize(serializedMessage);
	return &(RobotLostMessage::staticMessage);
}

RobotLostMessage::RobotLostMessage(int to, int from)
:Message(MESSAGE_ID, 1, to, from){}

RobotLostMessage::RobotLostMessage(char *serializedMessage){
	initialize(serializedMessage);
}

RobotLostMessage::~RobotLostMessage() {
}

int RobotLostMessage::getMessageLength(){
	return Message::getMessageLength();
}

void RobotLostMessage::readData(char **serializedMessageIndex){}

void RobotLostMessage::writeData(char **serializedMessageIndex){}


} /* namespace RobotProtocol */
