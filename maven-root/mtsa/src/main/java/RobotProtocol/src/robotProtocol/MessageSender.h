/*
 * MessageSender.h
 *
 *  Created on: Jan 31, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_MESSAGESENDER_H_
#define ROBOTPROTOCOL_MESSAGESENDER_H_

#include "Message.h"

namespace RobotProtocol {

class MessageSender {
public:
	MessageSender();
	virtual ~MessageSender();
	virtual bool sendMessage(char *messageToSend, int messageLength) = 0;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_MESSAGESENDER_H_ */
