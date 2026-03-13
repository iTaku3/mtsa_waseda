/*
 * ReadIRSensorsReplyMessage.h
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */
#ifndef ROBOTPROTOCOL_READIRSENSORSREPLYMESSAGE_H_
#define ROBOTPROTOCOL_READIRSENSORSREPLYMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class ReadIRSensorsReplyMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 209;

	static const int TOP_FIELD_LENGTH				= 4;
	static const int LEFT_FIELD_LENGTH				= 4;
	static const int RIGHT_FIELD_LENGTH			= 4;

	static Message *deserialize(char *serializedMessage);

	ReadIRSensorsReplyMessage(int to, int from, int leftValue, int rightValue, int topValue);
	ReadIRSensorsReplyMessage(char *serializedMessage);
	virtual ~ReadIRSensorsReplyMessage();
	virtual int getMessageLength();
	int getLeftValue();
	int getRightValue();
	int getTopValue();
	void setLeftValue(int leftValue);
	void setRightValue(int rightValue);
	void setTopValue(int topValue);
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	int leftValue;
	int rightValue;
	int topValue;
	static ReadIRSensorsReplyMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_READIRSENSORSREPLYMESSAGE_H_ */

