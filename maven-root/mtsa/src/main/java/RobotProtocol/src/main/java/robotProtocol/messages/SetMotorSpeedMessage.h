/*
 * SetMotorSpeedMessage.h
 *
 *  Created on: Apr 5, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_SETMOTORSPEEDMESSAGE_H_
#define ROBOTPROTOCOL_SETMOTORSPEEDMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class SetMotorSpeedMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 201;

	static const int LEFT_FIELD_LENGTH			= 4;
	static const int RIGHT_FIELD_LENGTH			= 4;

	static Message *deserialize(char *serializedMessage);

	SetMotorSpeedMessage(int to, int from, int leftSpeed, int rightSpeed);
	SetMotorSpeedMessage(char *serializedMessage);
	virtual ~SetMotorSpeedMessage();
	virtual int getMessageLength();
	int getLeftSpeed();
	int getRightSpeed();
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	int leftSpeed;
	int rightSpeed;
	static SetMotorSpeedMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_SETMOTORSPEEDMESSAGE_H_ */
