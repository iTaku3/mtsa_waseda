/*
 * TurnRightMessage.h
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_TURNRIGHTMESSAGE_H_
#define ROBOTPROTOCOL_TURNRIGHTMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class TurnRightMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 206;

	static Message *deserialize(char *serializedMessage);

	TurnRightMessage(int to, int from);
	TurnRightMessage(char *serializedMessage);
	virtual ~TurnRightMessage();
	virtual int getMessageLength();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	static TurnRightMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_TURNRIGHTMESSAGE_H_ */
