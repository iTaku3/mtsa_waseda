/*
 * CommandAcceptedMessage.cpp
 *
 *  Created on: Feb 3, 2013
 *      Author: mariano
 */

#include "CommandAcceptedMessage.h"
#include <stdlib.h>
#include <stdio.h>

namespace RobotProtocol {

CommandAcceptedMessage CommandAcceptedMessage::staticMessage = CommandAcceptedMessage(0,0,0);

Message *CommandAcceptedMessage::deserialize(char *serializedMessage){
	CommandAcceptedMessage::staticMessage.initialize(serializedMessage);
	return &(CommandAcceptedMessage::staticMessage);
}

CommandAcceptedMessage::CommandAcceptedMessage(int to, int from, int acceptedMid)
:Message(MESSAGE_ID, 1, to, from){
	this->acceptedMid	= acceptedMid;
}

CommandAcceptedMessage::CommandAcceptedMessage(char *serializedMessage){
	initialize(serializedMessage);
}

CommandAcceptedMessage::~CommandAcceptedMessage() {
}

int CommandAcceptedMessage::getMessageLength(){
	return Message::getMessageLength() + ACCEPTED_MID_FIELD_LENGTH;
}

int CommandAcceptedMessage::getAcceptedMid(){
	return acceptedMid;
}


void CommandAcceptedMessage::readData(char **serializedMessageIndex){
	char *serializedMessage	= *serializedMessageIndex;
	char midField[ACCEPTED_MID_FIELD_LENGTH + 1];
	midField[ACCEPTED_MID_FIELD_LENGTH]	= '\0';
	int index = 0;
	memcpy(midField, &(serializedMessage[index]), ACCEPTED_MID_FIELD_LENGTH);
	acceptedMid = (int)strtol(midField, NULL, 10);
	index 		+= ACCEPTED_MID_FIELD_LENGTH;
	(*serializedMessageIndex) += index;
}

void CommandAcceptedMessage::writeData(char **serializedMessageIndex){
	sprintf(*serializedMessageIndex,"%04d", acceptedMid);
	(*serializedMessageIndex) = (*serializedMessageIndex) + ACCEPTED_MID_FIELD_LENGTH;
}

} /* namespace RobotProtocol */
