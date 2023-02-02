################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/robotProtocol/messages/CommandAcceptedMessage.cpp \
../src/robotProtocol/messages/CommandErrorMessage.cpp \
../src/robotProtocol/messages/CommunicationStartAcknowledgeMessage.cpp \
../src/robotProtocol/messages/CommunicationStartMessage.cpp \
../src/robotProtocol/messages/CommunicationStopMessage.cpp \
../src/robotProtocol/messages/DestinationReachedMessage.cpp \
../src/robotProtocol/messages/FollowLineMessage.cpp \
../src/robotProtocol/messages/KeepAliveMessage.cpp \
../src/robotProtocol/messages/RobotLostMessage.cpp \
../src/robotProtocol/messages/SetMotorSpeedMessage.cpp \
../src/robotProtocol/messages/TurnAroundMessage.cpp \
../src/robotProtocol/messages/TurnLeftMessage.cpp \
../src/robotProtocol/messages/TurnRightMessage.cpp 

OBJS += \
./src/robotProtocol/messages/CommandAcceptedMessage.o \
./src/robotProtocol/messages/CommandErrorMessage.o \
./src/robotProtocol/messages/CommunicationStartAcknowledgeMessage.o \
./src/robotProtocol/messages/CommunicationStartMessage.o \
./src/robotProtocol/messages/CommunicationStopMessage.o \
./src/robotProtocol/messages/DestinationReachedMessage.o \
./src/robotProtocol/messages/FollowLineMessage.o \
./src/robotProtocol/messages/KeepAliveMessage.o \
./src/robotProtocol/messages/RobotLostMessage.o \
./src/robotProtocol/messages/SetMotorSpeedMessage.o \
./src/robotProtocol/messages/TurnAroundMessage.o \
./src/robotProtocol/messages/TurnLeftMessage.o \
./src/robotProtocol/messages/TurnRightMessage.o 

CPP_DEPS += \
./src/robotProtocol/messages/CommandAcceptedMessage.d \
./src/robotProtocol/messages/CommandErrorMessage.d \
./src/robotProtocol/messages/CommunicationStartAcknowledgeMessage.d \
./src/robotProtocol/messages/CommunicationStartMessage.d \
./src/robotProtocol/messages/CommunicationStopMessage.d \
./src/robotProtocol/messages/DestinationReachedMessage.d \
./src/robotProtocol/messages/FollowLineMessage.d \
./src/robotProtocol/messages/KeepAliveMessage.d \
./src/robotProtocol/messages/RobotLostMessage.d \
./src/robotProtocol/messages/SetMotorSpeedMessage.d \
./src/robotProtocol/messages/TurnAroundMessage.d \
./src/robotProtocol/messages/TurnLeftMessage.d \
./src/robotProtocol/messages/TurnRightMessage.d 


# Each subdirectory must supply rules for building sources it contributes
src/robotProtocol/messages/%.o: ../src/robotProtocol/messages/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/toolsRepo/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


