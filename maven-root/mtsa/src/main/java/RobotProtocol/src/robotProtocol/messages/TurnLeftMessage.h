/*
 * TurnLeftMessage.h
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_TURNLEFTMESSAGE_H_
#define ROBOTPROTOCOL_TURNLEFTMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class TurnLeftMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 205;

	static Message *deserialize(char *serializedMessage);

	TurnLeftMessage(int to, int from);
	TurnLeftMessage(char *serializedMessage);
	virtual ~TurnLeftMessage();
	virtual int getMessageLength();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	static TurnLeftMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_TURNLEFTMESSAGE_H_ */
