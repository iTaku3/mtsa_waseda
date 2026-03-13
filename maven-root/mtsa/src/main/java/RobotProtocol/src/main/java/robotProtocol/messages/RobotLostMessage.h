/*
 * RobotLostMessage.h
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_ROBOTLOSTMESSAGE_H_
#define ROBOTPROTOCOL_ROBOTLOSTMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class RobotLostMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 204;

	static Message *deserialize(char *serializedMessage);

	RobotLostMessage(int to, int from);
	RobotLostMessage(char *serializedMessage);
	virtual ~RobotLostMessage();
	virtual int getMessageLength();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	static RobotLostMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_ROBOTLOSTMESSAGE_H_ */
