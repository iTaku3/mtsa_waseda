/*
 * KeepAliveMessage.h
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_KEEPALIVEMESSAGE_H_
#define ROBOTPROTOCOL_KEEPALIVEMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class KeepAliveMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 911;

	static const int BATTERY_FIELD_LENGTH		= 4;

	static Message *deserialize(char *serializedMessage);

	KeepAliveMessage();
	KeepAliveMessage(int to, int from, int batteryLeft);
	KeepAliveMessage(char *serializedMessage);
	virtual ~KeepAliveMessage();
	virtual int getMessageLength();
	int getBatteryLeft();
	void setBatteryLeft(int batteryLeft);
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	int batteryLeft;
	static KeepAliveMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_KEEPALIVEMESSAGE_H_ */
