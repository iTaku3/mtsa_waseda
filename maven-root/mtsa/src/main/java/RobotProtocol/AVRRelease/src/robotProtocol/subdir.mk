################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/robotProtocol/Command.cpp \
../src/robotProtocol/Message.cpp \
../src/robotProtocol/MessageManager.cpp \
../src/robotProtocol/MessageReceiver.cpp \
../src/robotProtocol/MessageSender.cpp \
../src/robotProtocol/N6.cpp \
../src/robotProtocol/SerialSenderReceiver.cpp 

OBJS += \
./src/robotProtocol/Command.o \
./src/robotProtocol/Message.o \
./src/robotProtocol/MessageManager.o \
./src/robotProtocol/MessageReceiver.o \
./src/robotProtocol/MessageSender.o \
./src/robotProtocol/N6.o \
./src/robotProtocol/SerialSenderReceiver.o 

CPP_DEPS += \
./src/robotProtocol/Command.d \
./src/robotProtocol/Message.d \
./src/robotProtocol/MessageManager.d \
./src/robotProtocol/MessageReceiver.d \
./src/robotProtocol/MessageSender.d \
./src/robotProtocol/N6.d \
./src/robotProtocol/SerialSenderReceiver.d 


# Each subdirectory must supply rules for building sources it contributes
src/robotProtocol/%.o: ../src/robotProtocol/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/toolsRepo/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


