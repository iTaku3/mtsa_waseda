/*
 * FollowLineCommand.h
 *
 *  Created on: Apr 16, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_FOLLOWLINECOMMAND_H_
#define ROBOTPROTOCOL_FOLLOWLINECOMMAND_H_

#include "../Command.h"
#include "../messages/FollowLineMessage.h"
#include "../messages/DestinationReachedMessage.h"
#include "../messages/RobotLostMessage.h"
//#include "../messages/RobotStatusMessage.h"
#include "../messages/ReusableMessageProxy.h"

namespace RobotProtocol {

class FollowLineCommand: public RobotProtocol::Command {
public:
	static const int FOLLOW_SPEED	= 20;
	static const int FIXING_SPEED	= 32;
	static const int START_DELAY	= 390;

	FollowLineCommand(N6* robot);
	virtual ~FollowLineCommand();
	virtual void reset();
	virtual void resetFromMessage(Message* message);
	virtual void execute(int updateGap);
	virtual bool isContinuous();
private:
	int leftSensor;
	int rightSensor;
	int step;
	int currentDelay;
	bool starting;
};


} /* namespace RobotProtocol */

#endif /* ROBOTPROTOCOL_FOLLOWLINECOMMAND_H_ */

