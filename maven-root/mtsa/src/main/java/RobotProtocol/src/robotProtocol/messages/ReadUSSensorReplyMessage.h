/*
 * ReadIRSensorsReplyMessage.h
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */
#ifndef ROBOTPROTOCOL_READUSSENSORREPLYMESSAGE_H_
#define ROBOTPROTOCOL_READUSSENSORREPLYMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class ReadUSSensorReplyMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 211;

	static const int DISTANCE_FIELD_LENGTH			= 4;

	static Message *deserialize(char *serializedMessage);

	ReadUSSensorReplyMessage(int to, int from, int distance);
	ReadUSSensorReplyMessage(char *serializedMessage);
	virtual ~ReadUSSensorReplyMessage();
	virtual int getMessageLength();
	int getDistance();
	void setDistance(int distance);
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	int distance;
	static ReadUSSensorReplyMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_READUSSENSORREPLYMESSAGE_H_ */

