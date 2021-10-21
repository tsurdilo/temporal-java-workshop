<?php

declare(strict_types=1);

namespace Temporal\Workshop\Notifications;

use Carbon\CarbonInterval;
use Psr\Log\LoggerInterface;
use Temporal\WorkshopUtils\Logger;
use Temporal\Workflow;

class NotifyUserAccountsWorkflow implements NotifyUserAccountsWorkflowInterface
{

    private LoggerInterface $logger;

    public function __construct()
    {
        $this->logger = new Logger();
    }

    public function notify(array $accountIds): \Generator
    {
        foreach ($accountIds as &$acid) {
            $this->log("PHP: notifying for:  %s", $acid);
            yield Workflow::timer(CarbonInterval::seconds(2));
        }

        return "done";
    }

    private function log(string $message, ...$arg)
    {
        // by default all error logs are forwarded to the application server log and docker log
        $this->logger->debug(sprintf($message, ...$arg));
    }
}