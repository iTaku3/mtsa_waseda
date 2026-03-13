/*
 * SetMotorSpeedCommand.h
 *
 *  Created on: Apr 8, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_SETMOTORSPEEDCOMMAND_H_
#define ROBOTPROTOCOL_SETMOTORSPEEDCOMMAND_H_

#include "../Command.h"
#include "../messages/SetMotorSpeedMessage.h"

namespace RobotProtocol {

class SetMotorSpeedCommand: public RobotProtocol::Command {
public:
	SetMotorSpeedCommand(N6* robot);
	virtual ~SetMotorSpeedCommand();
	virtual void reset();
	virtual void resetFromMessage(Message* message);
	virtual void execute(int updateGap);
	virtual bool isContinuous();
	void setLeftSpeed(int leftSpeed);
	void setRightSpeed(int rightSpeed);
	int getLeftSpeed();
	int getRightSpeed();
private:
	int leftSpeed;
	int rightSpeed;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_SETMOTORSPEEDCOMMAND_H_ */
