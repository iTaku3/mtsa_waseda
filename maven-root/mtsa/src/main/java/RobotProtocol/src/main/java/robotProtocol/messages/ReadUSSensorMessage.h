/*
 * ReadIRSensorsMessage.h
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_READUSSENSOR_MESSAGE_H_
#define ROBOTPROTOCOL_READUSSENSOR_MESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class ReadUSSensorMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 210;

	static Message *deserialize(char *serializedMessage);

	ReadUSSensorMessage(int to, int from);
	ReadUSSensorMessage(char *serializedMessage);
	virtual ~ReadUSSensorMessage();
	virtual int getMessageLength();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	static ReadUSSensorMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_READUSSENSOR_MESSAGE_H_ */
