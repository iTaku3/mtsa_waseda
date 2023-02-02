/*
 * SendKeepAliveCommand.h
 *
 *  Created on: Apr 8, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_SENDKEEPALIVECOMMAND_H_
#define ROBOTPROTOCOL_SENDKEEPALIVECOMMAND_H_

#include "../Command.h"
#include "../messages/SetMotorSpeedMessage.h"

namespace RobotProtocol {

class SendKeepAliveCommand: public RobotProtocol::Command {
public:
	SendKeepAliveCommand(N6 *robot, Message* message);
	virtual ~SendKeepAliveCommand();
	virtual void reset();
	virtual void resetFromMessage(Message* message);
	virtual void execute(int updateGap);
	virtual bool isContinuous();
private:
	Message* message;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_SENDKEEPALIVECOMMAND_H_ */
