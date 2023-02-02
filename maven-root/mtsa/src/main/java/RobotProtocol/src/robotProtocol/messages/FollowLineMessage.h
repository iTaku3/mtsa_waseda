/*
 * FollowLineMessage.h
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_FOLLOWLINEMESSAGE_H_
#define ROBOTPROTOCOL_FOLLOWLINEMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class FollowLineMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 202;

	static Message *deserialize(char *serializedMessage);

	FollowLineMessage(int to, int from);
	FollowLineMessage(char *serializedMessage);
	virtual ~FollowLineMessage();
	virtual int getMessageLength();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	static FollowLineMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_FOLLOWLINEMESSAGE_H_ */
