/*
 * TurnRightMessage.cpp
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#include "TurnRightMessage.h"

namespace RobotProtocol {

TurnRightMessage TurnRightMessage::staticMessage = TurnRightMessage(0,0);

Message *TurnRightMessage::deserialize(char *serializedMessage){
	TurnRightMessage::staticMessage.initialize(serializedMessage);
	return &(TurnRightMessage::staticMessage);
}

TurnRightMessage::TurnRightMessage(int to, int from)
:Message(MESSAGE_ID, 1, to, from){}

TurnRightMessage::TurnRightMessage(char *serializedMessage){
	initialize(serializedMessage);
}

TurnRightMessage::~TurnRightMessage() {
}

int TurnRightMessage::getMessageLength(){
	return Message::getMessageLength();
}

void TurnRightMessage::readData(char **serializedMessageIndex){}

void TurnRightMessage::writeData(char **serializedMessageIndex){}



} /* namespace RobotProtocol */
