<?php

/**
 * This file is part of Temporal package.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

declare(strict_types=1);

namespace Temporal\Workshop\Notifications;

use Carbon\CarbonInterval;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;
use Temporal\Client\WorkflowOptions;
use Temporal\WorkshopUtils\Command;

class ExecuteCommand extends Command
{
    protected const NAME = 'notification';
    protected const DESCRIPTION = 'Execute Temporal\Workshop\Notifications';

    public function execute(InputInterface $input, OutputInterface $output)
    {
        $workflow = $this->workflowClient->newWorkflowStub(
            NotifyUserAccountsWorkflowInterface::class,
            WorkflowOptions::new()
                ->withTaskQueue("myTaskQueue")
                ->withWorkflowExecutionTimeout(CarbonInterval::minute(2))
        );

        $customerIds = array("c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10");

        $output->writeln("Starting <comment>NotifyUserAccountsWorkflow</comment>... ");


        $run = $this->workflowClient->start($workflow, $customerIds);

//        $workflow = $this->workflowClient->newUntypedRunningWorkflowStub(
//            "3b423ecc-8250-48cc-b2cb-ef6f6a71a491",
//            "29c8da4e-756e-49d0-86cc-17442af6ccba"
//        );

        $output->writeln(
            sprintf(
                'Started: WorkflowID=<fg=magenta>%s</fg=magenta>, RunID=<fg=magenta>%s</fg=magenta>',
                $run->getExecution()->getID(),
                $run->getExecution()->getRunID(),
            )
        );

        $output->writeln(sprintf("Result:\n<info>%s</info>", $run->getResult()));

        return self::SUCCESS;
    }
}