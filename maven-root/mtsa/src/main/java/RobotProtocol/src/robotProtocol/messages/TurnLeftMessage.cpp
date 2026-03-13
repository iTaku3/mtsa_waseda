/*
 * TurnLeftMessage.cpp
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#include "TurnLeftMessage.h"

namespace RobotProtocol {

TurnLeftMessage TurnLeftMessage::staticMessage = TurnLeftMessage(0,0);

Message *TurnLeftMessage::deserialize(char *serializedMessage){
	TurnLeftMessage::staticMessage.initialize(serializedMessage);
	return &(TurnLeftMessage::staticMessage);
}

TurnLeftMessage::TurnLeftMessage(int to, int from)
:Message(MESSAGE_ID, 1, to, from){}

TurnLeftMessage::TurnLeftMessage(char *serializedMessage){
	initialize(serializedMessage);
}

TurnLeftMessage::~TurnLeftMessage() {
}

int TurnLeftMessage::getMessageLength(){
	return Message::getMessageLength();
}

void TurnLeftMessage::readData(char **serializedMessageIndex){}

void TurnLeftMessage::writeData(char **serializedMessageIndex){}



} /* namespace RobotProtocol */
