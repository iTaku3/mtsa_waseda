/*
 * ReadIRSensorsMessage.h
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_READIRSENSOR_MESSAGE_H_
#define ROBOTPROTOCOL_READIRSENSOR_MESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class ReadIRSensorsMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 208;

	static Message *deserialize(char *serializedMessage);

	ReadIRSensorsMessage(int to, int from);
	ReadIRSensorsMessage(char *serializedMessage);
	virtual ~ReadIRSensorsMessage();
	virtual int getMessageLength();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	static ReadIRSensorsMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_READIRSENSOR_MESSAGE_H_ */
