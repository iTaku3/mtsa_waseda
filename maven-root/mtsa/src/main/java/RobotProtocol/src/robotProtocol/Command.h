/*
 * Command.h
 *
 *  Created on: Apr 5, 2013
 *      Author: mariano
 */

#ifndef ROBOTPROTOCOL_COMMAND_H_
#define ROBOTPROTOCOL_COMMAND_H_

#include "N6.h"
#include "Message.h"

namespace RobotProtocol {

//forward declaration for circular reference
class N6;

class Command {
public:
	Command(N6* robot);
	virtual ~Command();
	virtual void reset()							= 0;
	virtual void resetFromMessage(Message* message)	= 0;
	virtual void execute(int updateGap) 			= 0;
	bool isDone();
	virtual bool isContinuous()						= 0;
protected:
	N6*	robot;
	bool done;
};

} /* namespace RobotProtocol */
#endif /* ROBOTPROTOCOL_COMMAND_H_ */
