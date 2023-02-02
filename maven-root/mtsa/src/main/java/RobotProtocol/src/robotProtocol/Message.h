/*
 * Message.h
 *
 *  Created on: Jan 30, 2013
 *      Author: mariano
 *      Messages are formed as:
 *      BYTE	NAME			Description
 *      ===================================
 *      [HEADER PART]
 *      0-3		length			the total length of the message
 *      4-7		MID				message type ID
 *      8-10	Revision		message revision value
 *      11		Flags			message setup flag bit 0 is No Ack
 *      12-17	Spare			for future use
 *      [DATA PART]
 *      If any data should be send the first two bytes indicate
 *      the next parameter length, then the value provided will
 *      determine that this number of bytes follow containing the
 *      parameter value, then if any other parameter is to be send
 *      the second parameter length and value will follow, and so on
 *      [TERMINATION]
 *      termination is marked with a null character, this value is
 *      not be considered when setting the message length
 *
 *      When serializing/deserializing the parameter passed should
 *      be
 */

#ifndef ROBOTPROTOCOL_MESSAGE_H_
#define ROBOTPROTOCOL_MESSAGE_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>


namespace RobotProtocol {

class Message {
public:
	static const char *MESSAGE_HEAD_VALUE;

	static const int MESSAGE_LENGTH_ERROR		= -2;
	static const int MESSAGE_END_CHAR_NOT_FOUND	= -3;
	static const int MESSAGE_ID_UNKNOWN			= -4;
	static const int MESSAGE_REJECTED			= -5;
	static const int MASTER_UNDEFINED			= -6;
	static const int COMMAND_ID_UNKNOWN		= -7;

	static const int MESSAGE_HEADER_LENGTH		= 26;

	static const int HEAD_FIELD_LENGTH			= 6;
	static const int LENGTH_FIELD_LENGTH		= 4;
	static const int MID_FIELD_LENGTH			= 4;
	static const int REVISION_FIELD_LENGTH		= 3;
	static const int FLAGS_FIELD_LENGTH			= 1;
	static const int TO_FIELD_LENGTH			= 4;
	static const int FROM_FIELD_LENGTH			= 4;

	Message(int messageId = 0, int revision = 0, int to = 0xFFFF, int from = 0x0000, int flags = 0);
	Message(char *serializedMessage);
	virtual ~Message();

	int getMessageID();
	int getRevision();
	int getFlags();
	int getToAddress();
	int getFromAddress();
	void setToAddress(int toAddress);
	void setFromAddress(int fromAddress);
	virtual int getMessageLength();

	void serializeMessage(char *serializedMessage);
protected:
	int messageID;
	int revision;
	int flags;
	int to;
	int from;

	void initialize(char *serializedMessage);

	void readHeader(char **serializedMessageIndex);
	virtual void readData(char **serializedMessageIndex) = 0;
	void readTermination(char **serializedMessageIndex);

	void writeHeader(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex) = 0;
	void writeTermination(char **serializedMessageIndex);
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_MESSAGE_H_ */
