/*
 * Message.cpp
 *
 *  Created on: Jan 30, 2013
 *      Author: mariano
 */

#include "Message.h"

namespace RobotProtocol {

const char *Message::MESSAGE_HEAD_VALUE		= "LAFHIS";

Message::Message(int messageId, int revision, int to, int from, int flags) {
	this->messageID	= messageId;
	this->revision	= revision;
	this->flags		= flags;
	this->to		= to;
	this->from		= from;
}

Message::Message(char *serializedMessage){
	initialize(serializedMessage);
}

Message::~Message() {
}

int Message::getMessageLength(){
	return MESSAGE_HEADER_LENGTH;
}

int Message::getMessageID(){
	return this->messageID;
}

int Message::getRevision(){
	return this->revision;
}

int Message::getFlags(){
	return this->flags;
}

int Message::getToAddress(){
	return this->to;
}

int Message::getFromAddress(){
	return this->from;
}

void Message::setToAddress(int to){
	this-> to	= to;
}

void Message::setFromAddress(int from){
	this->from	= from;
}

void Message::serializeMessage(char *serializedMessage){
	char **serializedMessageIndex = &serializedMessage;
	writeHeader(serializedMessageIndex);
	writeData(serializedMessageIndex);
}

void Message::initialize(char *serializedMessage){
	char *initialIndex = serializedMessage;
	readHeader(&serializedMessage);
	readData(&serializedMessage);
#ifndef AVR
	if(serializedMessage - initialIndex != getMessageLength())
		throw MESSAGE_LENGTH_ERROR;
#endif
}

void Message::readHeader(char **serializedMessageIndex){
	char *serializedMessage	= *serializedMessageIndex;
	char midField[MID_FIELD_LENGTH + 1];
	char revField[REVISION_FIELD_LENGTH + 1];
	char flagsField[FLAGS_FIELD_LENGTH + 1];
	char toField[TO_FIELD_LENGTH + 1];
	char fromField[FROM_FIELD_LENGTH + 1];
	midField[MID_FIELD_LENGTH] = revField[REVISION_FIELD_LENGTH] = flagsField[FLAGS_FIELD_LENGTH] = '\0';
	toField[TO_FIELD_LENGTH] = fromField[FROM_FIELD_LENGTH] = '\0';
	int index = 0;
	//int messageLength = (int)strtol(*serializedMessageIndex, &next, LENGTH_FIELD_LENGTH);
	index += HEAD_FIELD_LENGTH + LENGTH_FIELD_LENGTH;
	memcpy(midField, &(serializedMessage[index]), MID_FIELD_LENGTH);
	messageID = (int)strtol(midField, NULL, 10);
	index += MID_FIELD_LENGTH;
	memcpy(revField, &(serializedMessage[index]), REVISION_FIELD_LENGTH);
	revision = (int)strtol(revField, NULL, 10);
	index += REVISION_FIELD_LENGTH;
	memcpy(flagsField, &(serializedMessage[index]), FLAGS_FIELD_LENGTH);
	flags = (int)strtol(flagsField, NULL, 10);
	index += FLAGS_FIELD_LENGTH;
	memcpy(toField, &(serializedMessage[index]), TO_FIELD_LENGTH);
	to = (int)strtol(toField, NULL, 10);
	index += TO_FIELD_LENGTH;
	memcpy(fromField, &(serializedMessage[index]), FROM_FIELD_LENGTH);
	from = (int)strtol(fromField, NULL, 10);
	index += FROM_FIELD_LENGTH;
	(*serializedMessageIndex) += index;
	/*
	char* next;
	char midField[MID_FIELD_LENGTH];
	char revField[REVISION_FIELD_LENGTH];
	char flagsField[FLAGS_FIELD_LENGTH];
	int index = 0;

	//int messageLength = (int)strtol(*serializedMessageIndex, NULL, 10);
	(*serializedMessageIndex) += HEAD_FIELD_LENGTH + LENGTH_FIELD_LENGTH;
	memcpy(midField, *serializedMessageIndex, MID_FIELD_LENGTH);
	messageID = (int)strtol(midField, NULL, 10);
	(*serializedMessageIndex) += MID_FIELD_LENGTH;
	memcpy(revField, *serializedMessageIndex, REVISION_FIELD_LENGTH);
	revision = (int)strtol(revField, NULL, 10);
	(*serializedMessageIndex) += REVISION_FIELD_LENGTH;
	memcpy(flagsField, *serializedMessageIndex, FLAGS_FIELD_LENGTH);
	flags = (int)strtol(flagsField, &next, 10);
	(*serializedMessageIndex) += FLAGS_FIELD_LENGTH;
	//int spare = (int)strtol(*serializedMessageIndex, &next, 10);
	(*serializedMessageIndex) += SPARE_FIELD_LENGTH;
	*/
}

void Message::writeHeader(char **serializedMessageIndex){
	sprintf(*serializedMessageIndex,"%s%04d%04d%03d%01d%04d%04d", MESSAGE_HEAD_VALUE, getMessageLength(), messageID, revision, flags, to, from);
	(*serializedMessageIndex) = (*serializedMessageIndex) + MESSAGE_HEADER_LENGTH;
}

} /* namespace RobotProtocol */
