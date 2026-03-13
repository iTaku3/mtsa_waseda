/*
 * SerialSenderReceiver.h
 *
 *  Created on: Mar 27, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_SERIALSENDERRECEIVER_H_
#define ROBOTPROTOCOL_SERIALSENDERRECEIVER_H_

#define BASE_FREQ         440
#define BUZZER_PIN        23

#include <stdio.h>
#include <WProgram.h>
#include "MessageSender.h"
#include "MessageReceiver.h"


namespace RobotProtocol {

class SerialSenderReceiver: public RobotProtocol::MessageSender,
		public RobotProtocol::MessageReceiver {
public:
	SerialSenderReceiver();
	virtual ~SerialSenderReceiver();
	virtual bool hasPendingMessage();
	virtual void getNextMessage(char *incomingMessage, int &messageLength);
	virtual bool sendMessage(char *messageToSend, int messageLength);
private:
	static const int MAX_MESSAGE_LENGTH	= 70;
	int messageLength;
	char messageBuffer[MAX_MESSAGE_LENGTH];
	HardwareSerial	*serial;
	int readBytes;
	int length;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_SERIALSENDERRECEIVER_H_ */
