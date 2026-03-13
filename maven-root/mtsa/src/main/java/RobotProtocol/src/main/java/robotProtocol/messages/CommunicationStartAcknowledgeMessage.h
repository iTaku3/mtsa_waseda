/*
 * CommunicationStartAcknowledgeMessage.h
 *
 *  Created on: Feb 3, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_COMMUNICATIONSTARTACKNOWLEDGEMESSAGE_H_
#define ROBOTPROTOCOL_COMMUNICATIONSTARTACKNOWLEDGEMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class CommunicationStartAcknowledgeMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID 	= 2;

	static Message *deserialize(char *serializedMessage);

	CommunicationStartAcknowledgeMessage(int to, int from);
	CommunicationStartAcknowledgeMessage(char *serializedMessage);
	virtual ~CommunicationStartAcknowledgeMessage();
	virtual int getMessageLength();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	static CommunicationStartAcknowledgeMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_COMMUNICATIONSTARTACKNOWLEDGEMESSAGE_H_ */
