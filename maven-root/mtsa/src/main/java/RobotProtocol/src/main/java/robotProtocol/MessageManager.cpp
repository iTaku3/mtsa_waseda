/*
 * MessageManager.cpp
 *
 *  Created on: Jan 30, 2013
 *      Author: mariano
 */

#include "MessageManager.h"


namespace RobotProtocol {

MessageManager::MessageManager(MessageReceiver *messageReceiver, MessageSender *messageSender) {
	this->messageReceiver		= messageReceiver;
	this->messageSender			= messageSender;

	incomingMessagePoolSize		= 0;
	outgoingMessagePoolSize		= 0;

	lastDeserializerIndex		= -1;

	memset(messageBuffer, 0, MAX_MESSAGE_LENGTH);

	int i;

	for(i = 0; i < MAX_DESERIALIZER_AMOUNT; i++){
		deserializerMethods[i]	= NULL;
		deserializersMids[i]	= -1;
	}
}

MessageManager::~MessageManager() {
}


bool MessageManager::sendMessage(char *serializedMessage, int messageLength){
	return messageSender->sendMessage(serializedMessage, messageLength);
}

bool MessageManager::hasPendingMessage(){
	return messageReceiver->hasPendingMessage();
}

Message* MessageManager::deserialize(char *serializedMessage, int messageLength){
	int mid;
	char midIndex[MID_FIELD_LENGTH];

	if(messageLength <= 0){
		return NULL;
	}


	memcpy(midIndex, (char*)(serializedMessage + MID_POSITION), MID_FIELD_LENGTH);
	mid = (int)strtol(midIndex, NULL, 10);
	if(deserializerMethods[getMidIndex(mid)] == NULL){
		return NULL;
	}


	DeserializerFunctionPointer func	= (DeserializerFunctionPointer)deserializerMethods[getMidIndex(mid)];

	Message* receivedMessage			= func(serializedMessage);
	return receivedMessage;
}

Message* MessageManager::getNextMessage(){
	int messageLength;

	messageReceiver->getNextMessage(messageBuffer, messageLength);

	if(messageLength <= 0){
		return NULL;
	}

	return deserialize(messageBuffer, messageLength);
}

bool MessageManager::sendMessage(Message &messageToSend){
	int mid 				= messageToSend.getMessageID();
	int messageLength		= messageToSend.getMessageLength();

	int midIndex	= getMidIndex(mid);

	if(deserializerMethods[midIndex] == NULL){
		printf("No deserializer was set for MID:%i\n", mid);
		return false;
	}

	messageToSend.serializeMessage(messageBuffer);

	bool result 			= messageSender->sendMessage(
			messageBuffer, messageLength);
	return result;
}

int MessageManager::getMidIndex(int mid){
	if(lastDeserializerIndex < 0)
		return -1;

	for(int i = 0; i <= lastDeserializerIndex; i++){
		if(deserializersMids[i] == mid)
			return i;
	}
	return -1;
}

bool MessageManager::addDeserializer(int mid, DeserializerFunctionPointer deserializerMethod){
	if(mid < 0){
		printf("Mid %i was out of range\n", mid);
		return false;
	}

	int midIndex	= getMidIndex(mid);

	if(midIndex != -1){
		printf("Deserializer for Mid %i was already added\n", mid);
		return false;
	}

	lastDeserializerIndex++;

	deserializerMethods[lastDeserializerIndex]	= deserializerMethod;
	deserializersMids[lastDeserializerIndex]	= mid;

	return true;
}

} /* namespace RobotProtocol */
