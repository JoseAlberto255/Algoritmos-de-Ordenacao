CXX      = g++
CXXFLAGS = -std=c++17 -O2 -Iinclude
BUILD    = build

all: $(BUILD)/ordenacao $(BUILD)/busca

$(BUILD)/ordenacao: src/main_ordenacao.cpp $(wildcard include/*/*.hpp) | $(BUILD)
	$(CXX) $(CXXFLAGS) src/main_ordenacao.cpp -o $@

$(BUILD)/busca: src/main_busca.cpp $(wildcard include/*/*.hpp) | $(BUILD)
	$(CXX) $(CXXFLAGS) src/main_busca.cpp -o $@

$(BUILD):
	mkdir -p $(BUILD)

run-ordenacao: $(BUILD)/ordenacao
	./$(BUILD)/ordenacao

run-busca: $(BUILD)/busca
	./$(BUILD)/busca

clean:
	rm -rf $(BUILD)

.PHONY: all run-ordenacao run-busca clean