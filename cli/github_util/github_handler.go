package github_util

import (
	"context"
	"fmt"
	"os"

	log "github.com/sirupsen/logrus"

	"github.com/Alethon/ECE461_Part2/cli/metrics"
	"github.com/google/go-github/github"
	"golang.org/x/oauth2"
)

type OwnerName struct {
	Owner string
	Name  string
	Url   string
}

func errorExit(msg string, a ...any) {
	log.Errorf(msg, a...)
	fmt.Fprintf(os.Stderr, msg, a...)
	os.Exit(1)
}

func GetGithubModule(ownerName OwnerName) metrics.Module {
	ctx := context.Background()
	token, has_token := os.LookupEnv("API_KEY")
	if !has_token {
		errorExit("API_KEY variable not in environment, please set it in enviroment variables")
	}
	if len(token) == 0 {
		errorExit("API_KEY variable is present, but not set to a value")
	}

	ts := oauth2.StaticTokenSource(
		&oauth2.Token{AccessToken: token},
	)
	tc := oauth2.NewClient(ctx, ts)

	client := github.NewClient(tc)

	repos, _, err := client.Repositories.Get(ctx, ownerName.Owner, ownerName.Name)
	if err != nil {
		log.Debug(err)
	}

	opt := &github.ListContributorsOptions{
		ListOptions: github.ListOptions{PerPage: 30},
	}
	var allContributors []*github.Contributor
	for {
		contributors, resp, err := client.Repositories.ListContributors(ctx, ownerName.Owner, ownerName.Name, opt)
		if err != nil {
			log.Debug(err)
		}
		allContributors = append(allContributors, contributors...)
		if resp.NextPage == 0 {
			break
		}
		opt.Page = resp.NextPage
	}

	// Can create error with contributor stats and umarshalling, not consistent
	return GitHubModule{Repo: repos, Contributors: allContributors, Url: ownerName.Url}
}

// TODO: Handle recieving errors from github API, no need to panic move on to next OwnerName (log issue)
func GetGithubModules(ownerNames []OwnerName) []metrics.Module {
	res := []metrics.Module{}
	ctx := context.Background()
	token, has_token := os.LookupEnv("API_KEY")
	if !has_token {
		errorExit("API_KEY variable not in environment, please set it in enviroment variables")
	}
	if len(token) == 0 {
		errorExit("API_KEY variable is present, but not set to a value")
	}

	ts := oauth2.StaticTokenSource(
		&oauth2.Token{AccessToken: token},
	)
	tc := oauth2.NewClient(ctx, ts)

	client := github.NewClient(tc)

	for _, on := range ownerNames {
		repos, _, err := client.Repositories.Get(ctx, on.Owner, on.Name)
		if err != nil {
			log.Debug(err)
		}

		opt := &github.ListContributorsOptions{
			ListOptions: github.ListOptions{PerPage: 30},
		}
		var allContributors []*github.Contributor
		for {
			contributors, resp, err := client.Repositories.ListContributors(ctx, on.Owner, on.Name, opt)
			if err != nil {
				log.Debug(err)
			}
			allContributors = append(allContributors, contributors...)
			if resp.NextPage == 0 {
				break
			}
			opt.Page = resp.NextPage
		}

		// Can create error with contributor stats and umarshalling, not consistent
		module := GitHubModule{Repo: repos, Contributors: allContributors, Url: on.Url}
		res = append(res, module)

	}
	return res
}
