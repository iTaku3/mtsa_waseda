/*
 * MessageManager.h
 *
 *  Created on: Jan 30, 2013
 *      Author: mariano
 *
 *      Should handle message pool, message buffer
 *      message serialization deserialization etc
 */

#ifndef ROBOTPROTOCOL_MESSAGEMANAGER_H_
#define ROBOTPROTOCOL_MESSAGEMANAGER_H_

#include "Message.h"
#include "MessageSender.h"
#include "MessageReceiver.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

namespace RobotProtocol {

typedef Message* (*DeserializerFunctionPointer) (char*);

class MessageManager {
public:
	MessageManager(MessageReceiver *messageReceiver, MessageSender *messageSender);
	virtual ~MessageManager();
	bool sendMessage(char *serializedMessage, int messageLength);
	bool hasPendingMessage();
	Message* getNextMessage();
	bool sendMessage(Message &messageToSend);
	bool addDeserializer(int mid, DeserializerFunctionPointer deserializerMethod);
	Message* deserialize(char* serializedMessage, int messageLength);
protected:
	MessageReceiver *messageReceiver;
	MessageSender *messageSender;
private:
	static const int MID_POSITION				= 10;
	static const int MID_FIELD_LENGTH			= 4;
	//TODO: check this constraint
	static const int MAX_MESSAGE_LENGTH		= 70;
	static const int MAX_DESERIALIZER_AMOUNT	= 32;

	DeserializerFunctionPointer deserializerMethods[MAX_DESERIALIZER_AMOUNT];
	int deserializersMids[MAX_DESERIALIZER_AMOUNT];
	int lastDeserializerIndex;

	int incomingMessagePoolSize;
	int outgoingMessagePoolSize;

	char messageBuffer[MAX_MESSAGE_LENGTH];

	int getMidIndex(int mid);
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_MESSAGEMANAGER_H_ */
