/*
 * RobotStatusMessage.h
 *
 *  Created on: Jun 24, 2013
 *      Author: mariano
 */
#ifndef ROBOTPROTOCOL_ROBOTSTATUSMESSAGE_H_
#define ROBOTPROTOCOL_ROBOTSTATUSMESSAGE_H_

#include "../Message.h"

namespace RobotProtocol {

class RobotStatusMessage: public RobotProtocol::Message {
public:
	static const int MESSAGE_ID					= 211;

	static const int LEFT_SPEED_FIELD_LENGTH		= 4;
	static const int RIGHT_SPEED_FIELD_LENGTH		= 4;
	static const int LEFT_IR_FIELD_LENGTH			= 4;
	static const int RIGHT_IR_FIELD_LENGTH			= 4;
	static const int BATTERY_FIELD_LENGTH			= 4;

	static Message *deserialize(char *serializedMessage);

	RobotStatusMessage(int to, int from, int leftSpeedValue, int rightSpeedValue,
				int leftIRValue, int rightIRValue, int batteryValue);
	RobotStatusMessage(char *serializedMessage);
	virtual ~RobotStatusMessage();
	virtual int getMessageLength();
	int getLeftSpeedValue();
	int getRightSpeedValue();
	int getLeftIRValue();
	int getRightIRValue();
	int getBatteryValue();
	void setLeftSpeedValue(int leftSpeedValue);
	void setRightSpeedValue(int rightSpeedValue);
	void setLeftIRValue(int leftIRValue);
	void setRightIRValue(int rightIRValue);
	void setBatteryValue(int batteryValue);
protected:
	virtual void readData(char **serializedMessageIndex);
	virtual void writeData(char **serializedMessageIndex);
	int leftSpeedValue;
	int rightSpeedValue;
	int leftIRValue;
	int rightIRValue;
	int batteryValue;
	static RobotStatusMessage staticMessage;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_ROBOTSTATUSMESSAGE_H_ */

