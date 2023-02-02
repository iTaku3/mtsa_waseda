/*
 * MessageReceiver.h
 *
 *  Created on: Jan 31, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_MESSAGERECEIVER_H_
#define ROBOTPROTOCOL_MESSAGERECEIVER_H_

#include "Message.h"
#include <stdlib.h>
#include <string.h>

namespace RobotProtocol {

class MessageReceiver {
public:
	virtual bool hasPendingMessage() = 0;
	virtual void getNextMessage(char *incomingMessage, int &messageLength) = 0;
	MessageReceiver();
	virtual ~MessageReceiver();
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_MESSAGERECEIVER_H_ */
