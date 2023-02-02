/*
 * DestinationReachedMessage.cpp
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#include "DestinationReachedMessage.h"
#include <stdlib.h>
#include <stdio.h>

namespace RobotProtocol {

DestinationReachedMessage DestinationReachedMessage::staticMessage = DestinationReachedMessage(0,0);

Message *DestinationReachedMessage::deserialize(char *serializedMessage){
	DestinationReachedMessage::staticMessage.initialize(serializedMessage);
	return &(DestinationReachedMessage::staticMessage);
}

DestinationReachedMessage::DestinationReachedMessage(int to, int from)
:Message(MESSAGE_ID, 1, to, from){}

DestinationReachedMessage::DestinationReachedMessage(char *serializedMessage){
	initialize(serializedMessage);
}

DestinationReachedMessage::~DestinationReachedMessage() {
}

int DestinationReachedMessage::getMessageLength(){
	return Message::getMessageLength();
}

void DestinationReachedMessage::readData(char **serializedMessageIndex){}

void DestinationReachedMessage::writeData(char **serializedMessageIndex){}

} /* namespace RobotProtocol */
