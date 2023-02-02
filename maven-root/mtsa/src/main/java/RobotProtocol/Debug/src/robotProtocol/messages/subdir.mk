################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/robotProtocol/messages/AnalogSubscribeMessage.cpp \
../src/robotProtocol/messages/AnalogUnsubscribeMessage.cpp \
../src/robotProtocol/messages/AnalogValueAcknowledgeMessage.cpp \
../src/robotProtocol/messages/AnalogValueMessage.cpp \
../src/robotProtocol/messages/CommandAcceptedMessage.cpp \
../src/robotProtocol/messages/CommandErrorMessage.cpp \
../src/robotProtocol/messages/CommunicationStartAcknowledgeMessage.cpp \
../src/robotProtocol/messages/CommunicationStartMessage.cpp \
../src/robotProtocol/messages/CommunicationStopMessage.cpp \
../src/robotProtocol/messages/SetAnalogValueMessage.cpp 

OBJS += \
./src/robotProtocol/messages/AnalogSubscribeMessage.o \
./src/robotProtocol/messages/AnalogUnsubscribeMessage.o \
./src/robotProtocol/messages/AnalogValueAcknowledgeMessage.o \
./src/robotProtocol/messages/AnalogValueMessage.o \
./src/robotProtocol/messages/CommandAcceptedMessage.o \
./src/robotProtocol/messages/CommandErrorMessage.o \
./src/robotProtocol/messages/CommunicationStartAcknowledgeMessage.o \
./src/robotProtocol/messages/CommunicationStartMessage.o \
./src/robotProtocol/messages/CommunicationStopMessage.o \
./src/robotProtocol/messages/SetAnalogValueMessage.o 

CPP_DEPS += \
./src/robotProtocol/messages/AnalogSubscribeMessage.d \
./src/robotProtocol/messages/AnalogUnsubscribeMessage.d \
./src/robotProtocol/messages/AnalogValueAcknowledgeMessage.d \
./src/robotProtocol/messages/AnalogValueMessage.d \
./src/robotProtocol/messages/CommandAcceptedMessage.d \
./src/robotProtocol/messages/CommandErrorMessage.d \
./src/robotProtocol/messages/CommunicationStartAcknowledgeMessage.d \
./src/robotProtocol/messages/CommunicationStartMessage.d \
./src/robotProtocol/messages/CommunicationStopMessage.d \
./src/robotProtocol/messages/SetAnalogValueMessage.d 


# Each subdirectory must supply rules for building sources it contributes
src/robotProtocol/messages/%.o: ../src/robotProtocol/messages/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -Wall -g2 -gstabs -O0 -fpack-struct -fshort-enums -funsigned-char -funsigned-bitfields -fno-exceptions -mmcu=atmega16 -DF_CPU=1000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


