/*
 * CommandErrorMessage.cpp
 *
 *  Created on: Feb 3, 2013
 *      Author: mariano
 */

#include "CommandErrorMessage.h"
#include <stdlib.h>
#include <stdio.h>

namespace RobotProtocol {

CommandErrorMessage CommandErrorMessage::staticMessage = CommandErrorMessage(0,0,0,0);

Message *CommandErrorMessage::deserialize(char *serializedMessage){
	CommandErrorMessage::staticMessage.initialize(serializedMessage);
	return &(CommandErrorMessage::staticMessage);
}

CommandErrorMessage::CommandErrorMessage(int to, int from, int rejectedMid, int errorCode)
:Message(MESSAGE_ID, 1, to, from){
	this->rejectedMid	= rejectedMid;
	this->errorCode		= errorCode;
}

CommandErrorMessage::CommandErrorMessage(char *serializedMessage){
	initialize(serializedMessage);
}

CommandErrorMessage::~CommandErrorMessage() {
}

int CommandErrorMessage::getMessageLength(){
	return Message::getMessageLength() + REJECTED_MID_FIELD_LENGTH + ERROR_CODE_FIELD_LENGTH;
}

int CommandErrorMessage::getRejectedMid(){
	return rejectedMid;
}

int CommandErrorMessage::getErrorCode(){
	return errorCode;
}

void CommandErrorMessage::readData(char **serializedMessageIndex){
	char *serializedMessage	= *serializedMessageIndex;
	char rejectedField[REJECTED_MID_FIELD_LENGTH + 1];
	char errorField[ERROR_CODE_FIELD_LENGTH + 1];
	rejectedField[REJECTED_MID_FIELD_LENGTH] = errorField[ERROR_CODE_FIELD_LENGTH] = '\0';
	int index = 0;
	memcpy(rejectedField, &(serializedMessage[index]), REJECTED_MID_FIELD_LENGTH);
	rejectedMid = (int)strtol(rejectedField, NULL, 10);
	index 		+= REJECTED_MID_FIELD_LENGTH;
	memcpy(errorField, &(serializedMessage[index]), ERROR_CODE_FIELD_LENGTH);
	errorCode = (int)strtol(errorField, NULL, 10);
	index 		+= REJECTED_MID_FIELD_LENGTH;
	(*serializedMessageIndex) += index;
}

void CommandErrorMessage::writeData(char **serializedMessageIndex){
	sprintf(*serializedMessageIndex,"%04d%02d", rejectedMid, errorCode);
	(*serializedMessageIndex) = (*serializedMessageIndex) + REJECTED_MID_FIELD_LENGTH + ERROR_CODE_FIELD_LENGTH;
}

} /* namespace RobotProtocol */
