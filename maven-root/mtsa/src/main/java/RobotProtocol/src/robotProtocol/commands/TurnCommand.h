/*
 * TurnLeftCommand.h
 *
 *  Created on: Apr 17, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_TURNCOMMAND_H_
#define ROBOTPROTOCOL_TURNCOMMAND_H_

#include "../Command.h"
#include "../messages/TurnLeftMessage.h"
#include "../messages/TurnRightMessage.h"
#include "../messages/TurnAroundMessage.h"
#include "../messages/DestinationReachedMessage.h"
//#include "../messages/RobotStatusMessage.h"
#include "../messages/RobotLostMessage.h"
#include "../messages/ReusableMessageProxy.h"

namespace RobotProtocol {

class TurnCommand: public RobotProtocol::Command {
public:

	static const int TURN_SPEED		= 20;
	static const int FORTH_SPEED		= 25;
	static const int BACK_DELAY		= 110;
	static const int FORTH_DELAY	= 80;
	static const int BLIND_DELAY	= 900;

	TurnCommand(N6* robot);
	virtual ~TurnCommand();
	virtual void reset();
	virtual void resetFromMessage(Message* message);
	virtual void execute(int updateGap);
	virtual bool isContinuous();
private:
	int leftSensor;
	int rightSensor;
	int currentDelay;
	int step;
	int turnSpeed;
	int turnDelay;
};
} /* namespace RobotProtocol */

#endif /* ROBOTPROTOCOL_TURNCOMMAND_H_ */
