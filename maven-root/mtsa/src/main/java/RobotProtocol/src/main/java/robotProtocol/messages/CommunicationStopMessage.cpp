/*
 * CommunicationStopMessage.cpp
 *
 *  Created on: Feb 3, 2013
 *      Author: mariano
 */

#include "CommunicationStopMessage.h"

namespace RobotProtocol {

CommunicationStopMessage CommunicationStopMessage::staticMessage = CommunicationStopMessage(0,0);

Message *CommunicationStopMessage::deserialize(char *serializedMessage){
	CommunicationStopMessage::staticMessage.initialize(serializedMessage);
	return &(CommunicationStopMessage::staticMessage);
}

CommunicationStopMessage::CommunicationStopMessage(int to, int from)
:Message(MESSAGE_ID, 1, to, from){}

CommunicationStopMessage::CommunicationStopMessage(char *serializedMessage){
	initialize(serializedMessage);
}

CommunicationStopMessage::~CommunicationStopMessage() {
}

int CommunicationStopMessage::getMessageLength(){
	return Message::getMessageLength();
}

void CommunicationStopMessage::readData(char **serializedMessageIndex){}
void CommunicationStopMessage::writeData(char **serializedMessageIndex){}

} /* namespace RobotProtocol */
