/*
 * FollowLineMessage.cpp
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#include "FollowLineMessage.h"
#include <stdlib.h>
#include <stdio.h>

namespace RobotProtocol {

FollowLineMessage FollowLineMessage::staticMessage = FollowLineMessage(0,0);

Message *FollowLineMessage::deserialize(char *serializedMessage){
	FollowLineMessage::staticMessage.initialize(serializedMessage);
	return &(FollowLineMessage::staticMessage);
}

FollowLineMessage::FollowLineMessage(int to, int from)
:Message(MESSAGE_ID, 1, to, from){}

FollowLineMessage::FollowLineMessage(char *serializedMessage){
	initialize(serializedMessage);
}

FollowLineMessage::~FollowLineMessage() {
}

int FollowLineMessage::getMessageLength(){
	return Message::getMessageLength();
}

void FollowLineMessage::readData(char **serializedMessageIndex){}

void FollowLineMessage::writeData(char **serializedMessageIndex){}

} /* namespace RobotProtocol */
