/*
 * TurnLeftMessage.cpp
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#include "TurnAroundMessage.h"

namespace RobotProtocol {

TurnAroundMessage TurnAroundMessage::staticMessage = TurnAroundMessage(0,0);

Message *TurnAroundMessage::deserialize(char *serializedMessage){
	TurnAroundMessage::staticMessage.initialize(serializedMessage);
	return &(TurnAroundMessage::staticMessage);
}

TurnAroundMessage::TurnAroundMessage(int to, int from)
:Message(MESSAGE_ID, 1, to, from){}

TurnAroundMessage::TurnAroundMessage(char *serializedMessage){
	initialize(serializedMessage);
}

TurnAroundMessage::~TurnAroundMessage() {
}

int TurnAroundMessage::getMessageLength(){
	return Message::getMessageLength();
}

void TurnAroundMessage::readData(char **serializedMessageIndex){}

void TurnAroundMessage::writeData(char **serializedMessageIndex){}



} /* namespace RobotProtocol */
