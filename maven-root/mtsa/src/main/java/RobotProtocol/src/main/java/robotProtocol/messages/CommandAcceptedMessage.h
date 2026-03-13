/*
 * CommandAcceptedMessage.h
 *
 *  Created on: Feb 3, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_COMMANDACCEPTEDMESSAGE_H_
#define ROBOTPROTOCOL_COMMANDACCEPTEDMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class CommandAcceptedMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 5;

	static const int ACCEPTED_MID_FIELD_LENGTH	= 4;

	static Message *deserialize(char *serializedMessage);

	CommandAcceptedMessage(int to, int from, int acceptedMid);
	CommandAcceptedMessage(char *serializedMessage);
	virtual ~CommandAcceptedMessage();
	virtual int getMessageLength();
	int getAcceptedMid();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	int acceptedMid;
	static CommandAcceptedMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_COMMANDACCEPTEDMESSAGE_H_ */
