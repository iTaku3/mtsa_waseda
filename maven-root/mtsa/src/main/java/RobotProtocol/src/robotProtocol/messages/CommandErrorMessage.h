/*
 * CommandErrorMessage.h
 *
 *  Created on: Feb 3, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_COMMANDERRORMESSAGE_H_
#define ROBOTPROTOCOL_COMMANDERRORMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class CommandErrorMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 4;

	static const int REJECTED_MID_FIELD_LENGTH	= 4;
	static const int ERROR_CODE_FIELD_LENGTH	= 2;

	static Message *deserialize(char *serializedMessage);

	CommandErrorMessage(int to, int from, int rejectedMid, int errorCode);
	CommandErrorMessage(char *serializedMessage);
	virtual ~CommandErrorMessage();
	virtual int getMessageLength();
	int getRejectedMid();
	int getErrorCode();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	int rejectedMid;
	int errorCode;
	static CommandErrorMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_COMMANDERRORMESSAGE_H_ */
