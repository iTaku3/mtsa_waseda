/*
 * TurnLeftMessage.h
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_TURNAROUNDMESSAGE_H_
#define ROBOTPROTOCOL_TURNAROUNDMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class TurnAroundMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 207;

	static Message *deserialize(char *serializedMessage);

	TurnAroundMessage(int to, int from);
	TurnAroundMessage(char *serializedMessage);
	virtual ~TurnAroundMessage();
	virtual int getMessageLength();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	static TurnAroundMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_TURNAROUNDMESSAGE_H_ */
