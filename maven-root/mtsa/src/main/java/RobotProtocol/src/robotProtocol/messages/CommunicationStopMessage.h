/*
 * CommunicationStopMessage.h
 *
 *  Created on: Feb 3, 2013
 *      Author: mariano
 *
 *      should reply command accepted
 */

#ifndef ROBOTPROTOCOL_COMMUNICATIONSTOPMESSAGE_H_
#define ROBOTPROTOCOL_COMMUNICATIONSTOPMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class CommunicationStopMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID	= 3;

	static Message *deserialize(char *serializedMessage);

	CommunicationStopMessage(int to, int from);
	CommunicationStopMessage(char *serializedMessage);
	virtual ~CommunicationStopMessage();
	virtual int getMessageLength();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	static CommunicationStopMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_COMMUNICATIONSTOPMESSAGE_H_ */
