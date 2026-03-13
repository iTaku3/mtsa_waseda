/*
 * DestinationReachedMessage.h
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_DESTINATIONREACHEDMESSAGE_H_
#define ROBOTPROTOCOL_DESTINATIONREACHEDMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class DestinationReachedMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 203;

	static Message *deserialize(char *serializedMessage);

	DestinationReachedMessage(int to, int from);
	DestinationReachedMessage(char *serializedMessage);
	virtual ~DestinationReachedMessage();
	virtual int getMessageLength();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	static DestinationReachedMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_DESTINATIONREACHEDMESSAGE_H_ */
