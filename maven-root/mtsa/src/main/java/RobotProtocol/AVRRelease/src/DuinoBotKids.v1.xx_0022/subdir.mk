################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/DuinoBotKids.v1.xx_0022/CDCSerial.cpp \
../src/DuinoBotKids.v1.xx_0022/CPPLib.cpp \
../src/DuinoBotKids.v1.xx_0022/HardwareSerial.cpp \
../src/DuinoBotKids.v1.xx_0022/Print.cpp \
../src/DuinoBotKids.v1.xx_0022/Tone.cpp \
../src/DuinoBotKids.v1.xx_0022/VirtualSerial.cpp \
../src/DuinoBotKids.v1.xx_0022/WMath.cpp \
../src/DuinoBotKids.v1.xx_0022/WString.cpp \
../src/DuinoBotKids.v1.xx_0022/wiring.cpp 

C_SRCS += \
../src/DuinoBotKids.v1.xx_0022/Descriptors.c \
../src/DuinoBotKids.v1.xx_0022/WInterrupts.c \
../src/DuinoBotKids.v1.xx_0022/pins_arduino.c \
../src/DuinoBotKids.v1.xx_0022/wiring_analog.c \
../src/DuinoBotKids.v1.xx_0022/wiring_digital.c \
../src/DuinoBotKids.v1.xx_0022/wiring_pulse.c \
../src/DuinoBotKids.v1.xx_0022/wiring_shift.c 

OBJS += \
./src/DuinoBotKids.v1.xx_0022/CDCSerial.o \
./src/DuinoBotKids.v1.xx_0022/CPPLib.o \
./src/DuinoBotKids.v1.xx_0022/Descriptors.o \
./src/DuinoBotKids.v1.xx_0022/HardwareSerial.o \
./src/DuinoBotKids.v1.xx_0022/Print.o \
./src/DuinoBotKids.v1.xx_0022/Tone.o \
./src/DuinoBotKids.v1.xx_0022/VirtualSerial.o \
./src/DuinoBotKids.v1.xx_0022/WInterrupts.o \
./src/DuinoBotKids.v1.xx_0022/WMath.o \
./src/DuinoBotKids.v1.xx_0022/WString.o \
./src/DuinoBotKids.v1.xx_0022/pins_arduino.o \
./src/DuinoBotKids.v1.xx_0022/wiring.o \
./src/DuinoBotKids.v1.xx_0022/wiring_analog.o \
./src/DuinoBotKids.v1.xx_0022/wiring_digital.o \
./src/DuinoBotKids.v1.xx_0022/wiring_pulse.o \
./src/DuinoBotKids.v1.xx_0022/wiring_shift.o 

C_DEPS += \
./src/DuinoBotKids.v1.xx_0022/Descriptors.d \
./src/DuinoBotKids.v1.xx_0022/WInterrupts.d \
./src/DuinoBotKids.v1.xx_0022/pins_arduino.d \
./src/DuinoBotKids.v1.xx_0022/wiring_analog.d \
./src/DuinoBotKids.v1.xx_0022/wiring_digital.d \
./src/DuinoBotKids.v1.xx_0022/wiring_pulse.d \
./src/DuinoBotKids.v1.xx_0022/wiring_shift.d 

CPP_DEPS += \
./src/DuinoBotKids.v1.xx_0022/CDCSerial.d \
./src/DuinoBotKids.v1.xx_0022/CPPLib.d \
./src/DuinoBotKids.v1.xx_0022/HardwareSerial.d \
./src/DuinoBotKids.v1.xx_0022/Print.d \
./src/DuinoBotKids.v1.xx_0022/Tone.d \
./src/DuinoBotKids.v1.xx_0022/VirtualSerial.d \
./src/DuinoBotKids.v1.xx_0022/WMath.d \
./src/DuinoBotKids.v1.xx_0022/WString.d \
./src/DuinoBotKids.v1.xx_0022/wiring.d 


# Each subdirectory must supply rules for building sources it contributes
src/DuinoBotKids.v1.xx_0022/%.o: ../src/DuinoBotKids.v1.xx_0022/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/home/mariano/Desktop/facultad/robotica/toolsRepo/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -funsigned-char -funsigned-bitfields -fno-exceptions -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/DuinoBotKids.v1.xx_0022/%.o: ../src/DuinoBotKids.v1.xx_0022/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"/home/mariano/Desktop/facultad/robotica/toolsRepo/RobotProtocol/src/DuinoBotKids.v1.xx_0022" -Wall -Os -std=gnu99 -funsigned-char -funsigned-bitfields -ffunction-sections -fdata-sections --pedantic -mmcu=atmega32u4 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


