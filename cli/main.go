/*
Copyright © 2023 NAME HERE <EMAIL ADDRESS>
*/
package main

import "fmt"
import "io"
import "os"

import "github.com/anthony-pei/ECE461/cli/cmd"
import log "github.com/sirupsen/logrus"

import "github.com/anthony-pei/ECE461/cli/file_handler"
import "github.com/anthony-pei/ECE461/cli/github_util"
import "github.com/anthony-pei/ECE461/cli/metrics"

/*
#cgo LDFLAGS: -lstdc++ -lm
#include <stdlib.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>
#include "NetScoreMetric.h"
*/
import "C"

//export CalculateNetScoreMetric
func CalculateNetScoreMetric(link string, metric *C.NetScoreMetric) C.bool {
	name := file_handler.GetOwnerNameFromLink(link) // Not error checking file name

	if name == nil {
		return C.bool(false)
	}

	module := github_util.GetGithubModule(*name)

	netscoreModule := metrics.NetScoreMetric{URL: module.GetGitHubUrl()}
	netscoreModule.CalculateScore(module)
	
	(*metric).License = C.float(netscoreModule.License)
	(*metric).Correctness = C.float(netscoreModule.Correctness)
	(*metric).BusFactor = C.float(netscoreModule.Busfactor)
	(*metric).RampUp = C.float(netscoreModule.Rampup)
	(*metric).ResponsiveMaintainer = C.float(netscoreModule.Responsiveness)
	(*metric).NetScore = C.float(netscoreModule.Netscore)

	return C.bool(true)
}

func logging_init() {
	lf, exists := os.LookupEnv("LOG_FILE")
	if !exists || len(lf) == 0 {
		log.SetOutput(io.Discard)
		return
	}
	level, exists := os.LookupEnv("LOG_LEVEL")
	if !exists {
		level = "0"
	}
	switch level {
	default:
		log.SetOutput(io.Discard)
		return
	case "1":
		log.SetLevel(log.InfoLevel)
	case "2":
		log.SetLevel(log.DebugLevel)
	}

	file, err := os.OpenFile(lf, os.O_WRONLY|os.O_CREATE|os.O_APPEND, 0644)
	if err != nil {
		fmt.Fprintln(os.Stderr, "Error opening LOG_FILE", err)
		log.Fatal(err)
	}
	log.SetOutput(file)
}

func main() {
	logging_init()
	cmd.Execute()
}

