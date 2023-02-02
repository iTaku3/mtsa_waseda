/*
 * CommunicationStartMessage.cpp
 *
 *  Created on: Jan 31, 2013
 *      Author: mariano
 */

#include "CommunicationStartMessage.h"

namespace RobotProtocol {

CommunicationStartMessage CommunicationStartMessage::staticMessage = CommunicationStartMessage(0,0);

Message *CommunicationStartMessage::deserialize(char *serializedMessage){
	CommunicationStartMessage::staticMessage.initialize(serializedMessage);
	return &(CommunicationStartMessage::staticMessage);
}

CommunicationStartMessage::CommunicationStartMessage(int to, int from)
:Message(MESSAGE_ID, 1, to, from){}


CommunicationStartMessage::CommunicationStartMessage(char *serializedMessage){
	initialize(serializedMessage);
}

CommunicationStartMessage::~CommunicationStartMessage() {
}

int CommunicationStartMessage::getMessageLength(){
	return Message::getMessageLength();
}

void CommunicationStartMessage::readData(char **serializedMessageIndex){}
void CommunicationStartMessage::writeData(char **serializedMessageIndex){}

} /* namespace RobotProtocol */
