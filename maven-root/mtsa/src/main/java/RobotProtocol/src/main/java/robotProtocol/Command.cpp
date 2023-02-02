/*
 * Command.cpp
 *
 *  Created on: Apr 5, 2013
 *      Author: mariano
 */

#include "Command.h"

namespace RobotProtocol {

Command::Command(N6* robot) {
	this->robot				= robot;
	done					= false;
}

Command::~Command() {
	// TODO Auto-generated destructor stub
}

bool Command::isDone(){
	return	done;
}

} /* namespace RobotProtocol */
