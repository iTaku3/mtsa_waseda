/*
 * CommunicationStartMessage.h
 *
 *  Created on: Jan 31, 2013
 *      Author: mariano
 *
 *      Should reply communication start acknowledge or
 *      error client already connected
 */

#ifndef ROBOTPROTOCOL_COMMUNICATIONSTARTMESSAGE_H_
#define ROBOTPROTOCOL_COMMUNICATIONSTARTMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class CommunicationStartMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID 	= 1;

	static Message *deserialize(char *serializedMessage);

	CommunicationStartMessage(int to, int from);
	CommunicationStartMessage(char *serializedMessage);
	virtual ~CommunicationStartMessage();
	virtual int getMessageLength();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	static CommunicationStartMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_COMMUNICATIONSTARTMESSAGE_H_ */
